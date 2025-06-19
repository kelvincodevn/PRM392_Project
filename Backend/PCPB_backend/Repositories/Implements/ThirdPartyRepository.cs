using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using DAOs;
using Repositories.Interfaces;

namespace Repositories.Implements
{
    public class ThirdPartyRepository : GenericRepository<ThirdParty>, IThirdPartyRepository
    {
        public ThirdPartyRepository(PCPBContext context) : base(context)
        {
        }
        public async Task<ThirdParty> CreateThirdParty(ThirdParty thirdParty)
        {
            await AddAsync(thirdParty);
            await _context.SaveChangesAsync();
            return thirdParty;
        }

        public async Task<ThirdParty> DeleteThirdParty(int id)
        {
            throw new NotImplementedException();
        }

        public Task<ThirdParty> GetAllThirdParty()
        {
            throw new NotImplementedException();
        }

        public Task<ThirdParty> GetThirdPartyByName(string name)
        {
            throw new NotImplementedException();
        }

        public Task<ThirdParty> UpdateThirdParty(ThirdParty thirdParty)
        {
            throw new NotImplementedException();
        }
    }
}