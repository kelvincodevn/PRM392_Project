using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using DAOs;

namespace Repositories.Interfaces
{
    public interface IThirdPartyRepository : IGenericRepository<ThirdParty>
    {
        public Task<ThirdParty> GetThirdPartyByName(string name);
        public Task<ThirdParty> GetAllThirdParty();
        public Task<ThirdParty> CreateThirdParty(ThirdParty thirdParty);
        public Task<ThirdParty> UpdateThirdParty(ThirdParty thirdParty);
        public Task<ThirdParty> DeleteThirdParty(int id);
    }
}